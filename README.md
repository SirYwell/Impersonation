# Impersonation

This project is for simple interactions between a chat service like Discord
and [Dialogflow](https://dialogflow.com). It supports multiple bot instances 
using a own thread per instance.

All bots are set up using a config file.

## Setup

To start the program, you can simply use the command line:

`$ java -jar impersonation-VERSION.jar <config>` where &lt;config&gt; is a json file
in the following format:

```json5
{
    "bots": [
        {
            "name": "Chat Service name",
            "chat_service": "java.class.implementing.ChatService"
            // further settings, see documentation of the implementing chat service
        }
    ]
}
```

For example, if you want to have a discord bot:

```json
{
    "bots": [
        {
            "name": "My Super Intelligent Discord Bot",
            "chat_service": "de.hannesgreule.chat.impersonation.discord.DiscordChatService",
            "discord_token": "your token here",
            "dialogflow_project": "your project id here",
            "google_credentials": "your google credentials file here"
        }
    ]
}
```

It's simple, isn't it?

## TODOs

* Add Telegram support
* Add support for inlining google credentials
* Abstract the project into chat service and impersonation

## Contributing

Feel free to add cool stuff. Just create a Pull Request and let's discuss!
