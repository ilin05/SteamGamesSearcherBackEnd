# Please install OpenAI SDK first: `pip3 install openai`
import sys
from openai import OpenAI

# if len(sys.argv) < 2:
#     print("Please provide the content as a command line argument.")
#     sys.exit(1)
#
# content = sys.argv[1]
# print("I type in the title, description, tags, categories, genres of a game, please help me generate a guide about this game. " + content)

with open('src\main\python\content.txt', 'r', encoding='utf-8') as file:
    content = file.read()

client = OpenAI(api_key="sk-f7241070a52a4623ac07b56ceb262ec2", base_url="https://api.deepseek.com")

response = client.chat.completions.create(
    model="deepseek-chat",
    messages=[
        {"role": "system", "content": "You are a helpful assistant"},
        {"role": "user", "content": "I type in the title, description, tags, categories, genres of the game, please help me generate a guide about this game, complete it in one paragraph. " + content},
    ],
    stream=False
)

print(response.choices[0].message.content)