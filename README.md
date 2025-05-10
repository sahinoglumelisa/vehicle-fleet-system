# vehicle-fleet-system
A monolith, full-stack spring boot web application which is written to manage a vehicle fleet rental system

## How to take a build?
### Things to download:
    - Docker
    - Intellij Idea
    - Amazon Coretta 17 Java SDK (If java is not being installed!!!)
    - Postgresql with pgAdmin4

#### Steps to Run App

- First, download the intellij and get the git repo clone. Then open it in intellij.
- Open compose.yaml file and change POSTGRES_PASSWORD field to your pgAdmin4 password
- Then open terminal, and write "docker compose up -d"
- Then click on run button and you will most probably going to see, the app is running perfectly
- I suggest you to clone mel branch instead of main, or mine(deniz).