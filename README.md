Crowdcat

# Oauth Identity Provider Setup

Google OAuth creds page::
https://console.developers.google.com/apis/credentials

# Configuring OAuth service provider

Google API keys are passed to the app via environment variables set before starting the app

export CROWDCAT_GOOGLE_API_KEY="xxxxx"
export CROWDCAT_GOOGLE_SECRET="xxxxx"

Usually these are set in a script that wraps the start command

