# SolutionsQA_Automation

Solutions QA automation across ITOM, ITSM, and GPT.

- **Java_qa** — Selenium + Java + TestNG + Maven
- **Python_qa** — Python, pytest, DeepEval for GPT scenarios

Reporting integrates with Grafana/ELK and Extent reports.

## Public repository — secrets and local files

This repo is intended to be **public**. Do not commit API keys, passwords, certificates, or environment-specific databases.

| What | Where to configure |
|------|-------------------|
| Java stack URLs and credentials | Copy `Java_qa/properties.config.example` → `Java_qa/properties.config` (gitignored) |
| AIOps OpenAPI / LLM settings | Copy `Java_qa/resources/ITOM/AIOPS/openApiproperties.config.example` → `openApiproperties.config` |
| Python / DeepEval | Copy `Python_qa/.env.example` → `Python_qa/.env`, or export the same variables |
| HelixGPT connection JSON templates | Replace `__...__` placeholders under `Java_qa/resources/ITSM/HelixGPT/` |
| IA TSO certificate | `Java_qa/resources/ITOM/IA/servercerts/tso.pem` (see README in that folder) |
| IA credential DBs | `credential.db` under `Java_qa/resources/ITOM/IA/**/database/` (local only) |
| ONNX semantic model | `Java_qa/resources/ITOM/AIOPS/model.onnx` (see `MODEL.md` in that folder) |

DeepEval writes `.deepeval` under the working directory; those paths are gitignored.

Copy `Java_qa/resources/ITOM/tenant-keys.json.example` → `tenant-keys.json` and fill in ADE keys for your stack.

### Security checks before push

```powershell
# Replace demo passwords in new files (optional maintenance script)
python scripts/sanitize_passwords.py
python scripts/find_password_literals.py   # should report TOTAL 0



Review `semgrep-results.json`. Remaining items are often GitHub Actions pinning (supply chain) or test-framework SSL/XXE patterns—not leaked credentials.

### If credentials were ever pushed to GitHub

Rotate **immediately** in Azure OpenAI, Microsoft Entra app secrets, Salesforce, SharePoint, ServiceNow, and any other systems involved. Removing files from a new commit does not erase them from git history; use [GitHub secret scanning](https://docs.github.com/en/code-security/secret-scanning) and history rewrite (`git filter-repo` / BFG) if a leak already occurred.

## Quick start (local)

```bash
# Java
cd Java_qa
cp properties.config.example properties.config
# edit properties.config for your stack

# Python
cd Python_qa
cp .env.example .env
# edit .env, then: pip install -r requirements.txt
```
