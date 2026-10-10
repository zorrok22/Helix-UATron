# ---- Stage 1: Build dependencies (cached layer) ----
#
# Build context must be the repository root (SolutionsQA_Automation), not Java_qa alone,
# so Python_qa can be copied to /Python_qa for DeepEval (see util.DeepEval: ../Python_qa/... from WORKDIR /app).
#
#   docker build -f Java_qa/Dockerfile -t solqa-automation .
#
# Use the repo-root .dockerignore so local venvs/caches (e.g. Python_qa/.venv)
# are not sent in the build context or copied with `COPY Python_qa`.
#
# Environment Variables (passed at runtime via docker run -e):
# - PRODUCT_TYPE (required): Product type - "itsm" or "itom" (default: itsm)
# - STACK_NAME (recommended): Stack name (e.g., ociintqa, ocisolqa2, auto3)
#                             If not provided, uses existing properties.config
# - HOST_DOMAIN (recommended): Host domain (e.g., qa.example.com)
#                              If not provided, uses existing properties.config
# - DWPC_URL (required if STACK_NAME/HOST_DOMAIN provided): DWPC URL (use empty string "" for default catalog)
# - ADMIN_USERNAME (required if STACK_NAME/HOST_DOMAIN provided): Admin username (e.g., hannah_admin)
# - ADMIN_PASSWORD (required if STACK_NAME/HOST_DOMAIN provided): Admin password (e.g., __USER_PASSWORD__)
# - PRODUCT_VERSION (required if STACK_NAME/HOST_DOMAIN provided): Product version (e.g., 25.3.00)
# - ARSERVER_ALIAS (required if STACK_NAME/HOST_DOMAIN provided): AR Server alias (e.g., onbmc-s)
# - ISMSP_ENABLED (required if STACK_NAME/HOST_DOMAIN provided): MSP enabled flag (true/false)
# - deployment_job_id (optional): Deployment job ID for Selenium Grid tracking
# - TEST_SUITE (optional): Test suite XML file to run (default: Apex_P0.xml)
# - PYTHON_EXEC (optional): Python binary for DeepEval (default: python3)
#
# PostgreSQL Database Configuration (optional):
# - To enable DB reporting config at runtime, set POSTGRES_PWD
# - Optional overrides: POSTGRES_URL, POSTGRES_USER, POSTGRES_DATABASE, POSTGRES_TABLE
#
# ITOM-specific variables (required only if PRODUCT_TYPE=itom):
# - ITSM_URL (required): ITSM URL override
# - ADE_PLATFORM_URL (required): ADE Platform URL
# - ADE_USERNAME (required): ADE tenant admin username
# - ADE_PASSWORD (required): ADE tenant admin password
# - ADE_SMART_GRAPH_URL (optional): ADE Smart Graph URL
# - CMDBURL (required): CMDB URL
#
# Example usage for ITSM:
#   docker run -e PRODUCT_TYPE=itsm -e STACK_NAME=ociintqa -e HOST_DOMAIN=qa.example.com \
#              -e DWPC_URL="" -e ADMIN_USERNAME=hannah_admin -e ADMIN_PASSWORD=__USER_PASSWORD__ \
#              -e PRODUCT_VERSION=25.3.00 -e ARSERVER_ALIAS=onbmc-s -e ISMSP_ENABLED=false myimage
#
# Example usage for ITOM:
#   docker run -e PRODUCT_TYPE=itom -e STACK_NAME=ociintqa -e HOST_DOMAIN=qa.example.com \
#              -e DWPC_URL="" -e ADMIN_USERNAME=hannah_admin -e ADMIN_PASSWORD=__USER_PASSWORD__ \
#              -e PRODUCT_VERSION=25.3.00 -e ARSERVER_ALIAS=onbmc-s -e ISMSP_ENABLED=false \
#              -e ITSM_URL="https://custom-itsm.com" \
#              -e ADE_PLATFORM_URL="https://ade-platform.com" \
#              -e ADE_USERNAME="ade_admin" -e ADE_PASSWORD="AdePass123" \
#              -e ADE_SMART_GRAPH_URL="https://smart-graph.com" \
#              -e CMDBURL="https://cmdb.com" \
#              -e TEST_SUITE=ITOM_Sanity.xml myimage
#
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy only pom.xml first (to leverage dependency cache)
COPY Java_qa/pom.xml .

# Resolve project dependencies (incl. test scope) for layer cache. Avoid
# dependency:go-offline — it pulls plugins/reports beyond what verify needs and
# inflates the copied ~/.m2 in the final image.
RUN mvn -B dependency:resolve

# Copy source and compile
COPY Java_qa/src ./src

# Verify build works, but skip tests (dependencies already cached)
RUN mvn clean verify -DskipTests \
    && find /root/.m2/repository -name "*.lastUpdated" -delete \
    && find /root/.m2/repository -name "_remote.repositories" -delete


# ---- Stage 2: Runtime (JDK + Maven + Python; slimmer than maven:* full image) ----
# Maven is copied from the builder image (same 3.9.6 as compile) instead of apt/curl.
FROM eclipse-temurin:17-jdk-jammy

ENV MAVEN_HOME=/usr/share/maven \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 python3-pip \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /usr/share/maven /usr/share/maven
RUN ln -sf "${MAVEN_HOME}/bin/mvn" /usr/bin/mvn

# Copy Maven dependencies from builder (cached layer)
COPY --from=builder /root/.m2 /root/.m2

# Copy compiled artifacts from builder
COPY --from=builder /app/target ./target

# Copy runtime configuration files and resources
COPY Java_qa/resources ./resources
COPY Java_qa/properties.config .
COPY Java_qa/*.xml .
COPY Java_qa/docker-entrypoint.sh .

# Python_qa at /Python_qa — used by Java DeepEval (../Python_qa from /app) and other Python helpers
COPY Python_qa /Python_qa

# DeepEvalForJava.py → DeepEvalUtil: third-party deps are deepeval (+ its transitive deps, incl. pytest).
# Full Python_qa/requirements.txt is not needed in this image (Selenium stack unused for that path).
# Pin LangChain to <1.0: deepeval 2.4.x imports langchain.schema, removed in langchain 1.x.
# (Jammy's pip is too old for --break-system-packages; install without it unless PEP 668 blocks.)
RUN python3 -m pip install -U pip \
    && python3 -m pip install \
        "deepeval==2.4.2" \
        "langchain==0.3.0"

# Create Reports directory; normalize entrypoint line endings
RUN mkdir -p Reports && \
    chmod +x docker-entrypoint.sh && \
    sed -i 's/\r$//' docker-entrypoint.sh

ENV TEST_SUITE=Apex_P0.xml
ENV PYTHON_EXEC=python3

# Entrypoint performs runtime configuration before running tests
ENTRYPOINT ["/app/docker-entrypoint.sh"]
CMD ["mvn", "test"]
