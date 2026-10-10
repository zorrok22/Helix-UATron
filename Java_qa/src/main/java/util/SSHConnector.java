package util;

import com.jcraft.jsch.*;


import java.io.*;
import java.util.List;
import java.util.concurrent.*;

public class SSHConnector {
    private static final SolqaLogger log = new SolqaLogger(SSHConnector.class.getName());
    private Session session;
    private ChannelShell channel;
    private InputStream in;
    private PrintWriter writer;
    private final CommonWaits commonwait = new CommonWaits();

    public void connect(String hostname, String username, String password, int port) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, hostname, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }

    public void openChannel() throws JSchException, IOException {
        channel = (ChannelShell) session.openChannel("shell");;
        in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();
        writer = new PrintWriter(out, true);
        channel.connect();

        // Wait for initial prompt or ready state
        read(1000);
    }

    public void write(String command) throws IOException {
        log.info(command);
        if (channel == null || !channel.isConnected()) {
            throw new IllegalStateException("Shell channel is not connected");
        }

        // Send command
        writer.println(command);
        writer.flush();

        // Wait and read response
        return;
    }

    public String read(int timeout) throws IOException {
        StringBuilder response = new StringBuilder();
        byte[] tmp = new byte[1024];

        long endTime = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < endTime) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                response.append(new String(tmp, 0, i));

                // Reset timeout if we're still getting data
                endTime = System.currentTimeMillis() + timeout;
            }

            if (channel.isClosed() || response.toString().contains("EOF")) {
                break;
            }
            commonwait.waitForSometime(100);
        }

        return response.toString();
    }

    public void closeChannel() {
        if (channel != null) {
            channel.disconnect();
        }
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    public void executeCommands(List<String> commands, long timeoutMillis) {

        try {
            // Establish SSH session
            for (String command : commands) {
                log.info("Executing command: " + command);
                ChannelExec channel_exec = (ChannelExec) session.openChannel("exec");
                channel_exec.setCommand(command);
                channel_exec.setErrStream(System.err);

                InputStream in = channel_exec.getInputStream();
                channel_exec.connect();

                // Create an executor service for timeout handling
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<String> future = executor.submit(() -> {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] tmp = new byte[8192];
                    int readLen;
                    while ((readLen = in.read(tmp)) > 0) {
                        outputStream.write(tmp, 0, readLen);
                    }
                    return outputStream.toString();
                });

                String output;
                try {
                    // Wait for the command to complete or timeout
                    output = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
                    log.info("Output: <" + output + ">");

                    // Get the exit status
                    int status = channel_exec.getExitStatus();
                    log.info("Exit Status: " + status + ">");

                    if (status == 0) {
                        log.info("Command executed successfully.");
                    } else {
                        log.info("Command failed with status: " + status);
                    }
                } catch (TimeoutException e) {
                    log.info("Command timed out after " + timeoutMillis + " milliseconds.");
                    future.cancel(true); // Interrupt the task
                    if (channel_exec.isConnected()) {
                        channel_exec.disconnect(); // Forcefully disconnect the channel
                    }
                } catch (ExecutionException e) {
                    log.info("Command execution failed: " + e.getCause().getMessage());
                } finally {
                    executor.shutdownNow(); // Shut down the executor
                }

                if (channel_exec.isConnected()) {
                    channel_exec.disconnect();
                }
                Thread.sleep(2000); // Sleep between commands
            }
        } catch (JSchException | InterruptedException | IOException e) {
            log.info("Exception: " + e.getMessage());
        }
    }

    /**
     * Executes a single command on the remote host and returns stdout, stderr and exit code.
     */
    public CommandResult executeSingleCommand(String command, long timeoutMillis) {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("SSH session is not connected");
        }
        log.info(("Execute command " + command));
        ChannelExec channel = null;
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        StringBuilder out = new StringBuilder();
        int exitCode = -1;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(errStream);
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[8192];
            long end = System.currentTimeMillis() + timeoutMillis;
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, tmp.length);
                    if (i < 0) break;
                    out.append(new String(tmp, 0, i));
                }
                if ((channel.isClosed() && in.available() == 0) || System.currentTimeMillis() > end) {
                    break;
                }
                commonwait.waitForSometime(100);
            }
            // allow a little time for exit status to be set
            for (int i = 0; i < 10 && exitCode == -1; i++) {
                exitCode = channel.getExitStatus();
                if (exitCode != -1) break;
                commonwait.waitForSometime(500);
            }
            if (exitCode == -1) {
                exitCode = channel.getExitStatus();
            }
        } catch (Exception e) {
            return new CommandResult(out.toString(), errStream.toString(), exitCode, e.getMessage());
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        String stdout = out.toString();
        String stderr = errStream.toString();
        return new CommandResult(stdout, stderr, exitCode, null);
    }

    public static class CommandResult {
        public final String stdout;
        public final String stderr;
        public final int exitCode;
        public final String error;

        public CommandResult(String stdout, String stderr, int exitCode, String error) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.exitCode = exitCode;
            this.error = error;
            log.info("stdout <" + stdout + ">");
            log.info("stderr <" + stderr + ">");
            log.info("exitCode <" + exitCode + ">");
            log.info("error <" + error + ">");
        }
    }

    /**
     * Transfers a file using SFTP.
     * @param sourcePath       source path (local when upload=true, remote when upload=false)
     * @param destinationPath  destination path (remote when upload=true, local when upload=false)
     * @param upload           true to push local->remote, false to pull remote->local
     */
    public void transferFile(String sourcePath, String destinationPath, boolean upload) {
        Channel channel = null;
        try {
            if (session == null || !session.isConnected()) {
                throw new IllegalStateException("SSH session is not connected");
            }
            channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            if (upload) {
                sftp.put(sourcePath, destinationPath);
                log.info("Uploaded local file " + sourcePath + " to remote path " + destinationPath);
            } else {
                sftp.get(sourcePath, destinationPath);
                log.info("Downloaded remote file " + sourcePath + " to local path " + destinationPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to transfer file: " + e.getMessage(), e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    /**
     * Backward-compatible upload helper (local -> remote).
     */
    public void transferFile(String localFilePath, String remoteFilePath) {
        transferFile(localFilePath, remoteFilePath, true);
    }
}
