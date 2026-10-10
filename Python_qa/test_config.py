import os


def _env(name: str, default: str = "") -> str:
    value = os.getenv(name)
    if value is None or not value.strip():
        return default
    return value.strip()


class TestConfig:
    # Selenium Configuration
    WAIT_TIMEOUT = int(_env("WAIT_TIMEOUT", "180"))

    # Azure OpenAI Configuration for DeepEval (set via environment variables)
    AZURE_OPENAI_ENDPOINT = _env("AZURE_OPENAI_ENDPOINT")
    AZURE_OPENAI_API_KEY = _env("AZURE_OPENAI_API_KEY")
    AZURE_OPENAI_DEPLOYMENT_NAME = _env("AZURE_OPENAI_DEPLOYMENT_NAME")
    AZURE_OPENAI_API_VERSION = _env("AZURE_OPENAI_API_VERSION", "2024-10-21")

    # Tenant / Teams bot (set via environment variables)
    TENANT_ID = _env("TENANT_ID")
    SolQA_Bot2_ID = _env("SOLQA_BOT2_ID")
    CLIENT_SECRET = _env("CLIENT_SECRET")
    RESOURCE_GROUP = _env("RESOURCE_GROUP")
    NEW_ENDPOINT = _env("NEW_ENDPOINT", "https://yourapp.example.com/api/messages")
    API_VERSION = _env("API_VERSION", "2023-09-15-preview")

    BOT_OBJECT_ID = _env("BOT_OBJECT_ID")
    CHANNEL_NAME = _env("CHANNEL_NAME", "MS Teams channel for collaboration agent")
    TEAMS_ENDPOINT = _env("TEAMS_ENDPOINT")

    TENANT_USERNAME = _env("TENANT_USERNAME")
    TENANT_PASSWORD = _env("TENANT_PASSWORD")

    ADE_ACCESSKEY = _env("ADE_ACCESSKEY")
    ADE_SECRETKEY = _env("ADE_SECRETKEY")

    ADEURL = _env("ADEURL", "https://tenant.qa.example.com")
    ITSMBASEURL = _env("ITSMBASEURL", "https://confcom-{}.qa.example.com")
    ISURL = _env("ISURL", "https://confcom-is.qa.example.com")

    HOSTNAME = _env("HOSTNAME")
    SITUATION_PREFIX = _env("SITUATION_PREFIX")

    BOT_NAME = _env("BOT_NAME", "UAtron_APP")
