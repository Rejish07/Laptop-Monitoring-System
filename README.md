💻 Laptop Monitoring System
A full-stack IoT application that monitors laptop health (CPU, RAM, Battery) in real-time. It uses a Python Agent to collect data and a Java Spring Boot Server to visualize it and send email alerts when critical thresholds are reached.

🚀 Features
Real-time Dashboard: Live updates every 5 seconds.

Health Analysis: Calculates a "Strength Score" (0-100).

Email Alerts: Sends warnings via Gmail when CPU > 90% or Battery < 15%.

Cross-Platform: Agent works on Windows, macOS, and Linux.

🛠 Prerequisites
Before running the project, ensure you have the following installed:

Java JDK 17+

Maven (Apache Maven)

Python 3.8+

Google Account (For email alerts)

⚙️ Configuration (Crucial Step)
1. Generate Gmail App Password
To allow the system to send emails, you cannot use your regular password.

Go to Google Account > Security.

Enable 2-Step Verification.

Search for "App Passwords".

Create one named "LaptopMonitor" and copy the 16-character code.

2. Configure the Backend
Open src/main/resources/application.properties and update it:

Properties

server.port=8082
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=abcdefghijklmnop  <-- PASTE 16-CHAR CODE HERE (No Spaces)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
🏃‍♂️ How to Run
Part 1: The Backend Server (Java)
🍎 For macOS / Linux
Open a terminal in the project root folder.

Run the server:

Bash

mvn spring-boot:run
Once started, open your browser to: http://localhost:8082

🪟 For Windows
Open Command Prompt (cmd) or PowerShell in the project folder.

Run the server:

DOS

mvn spring-boot:run
(Note: If you don't have global maven, use .\mvnw spring-boot:run)

Once started, open your browser to: http://localhost:8082

Part 2: The Monitoring Agent (Python)
Note: Open a NEW terminal window for this. Do not close the Java server.

🍎 For macOS / Linux
Navigate to the project folder.

Create and activate a virtual environment (first time only):

Bash

python3 -m venv venv
source venv/bin/activate
Install dependencies:

Bash

pip install psutil requests
Run the agent:

Bash

python3 agent/client_agent.py
🪟 For Windows
Navigate to the project folder.

Create and activate a virtual environment (first time only):

DOS

python -m venv venv
venv\Scripts\activate
Install dependencies:

DOS

pip install psutil requests
Run the agent:

DOS

python agent/client_agent.py
🧪 Testing the System
View Dashboard: Go to http://localhost:8082. You should see the progress bars moving.

Test Alerts:

Click the "Simulate Analysis" button on the web dashboard.

This forces high CPU/low battery data.

Check your email inbox for the alert!

❓ Troubleshooting
Error: Web server failed to start. Port 8082 was already in use.

Fix: Kill the existing process.

Mac: lsof -i :8082 then kill -9 <PID>

Windows: netstat -ano | findstr :8082 then taskkill /PID <PID> /F

Error: ModuleNotFoundError: No module named 'psutil'

Fix: You forgot to activate the virtual environment or install requirements.

Run source venv/bin/activate (Mac) or venv\Scripts\activate (Win) and try again.

Error: Authentication failed (Email)

Fix: You are likely using your real Gmail password. You must use the 16-character App Password generated in your Google Security settings.