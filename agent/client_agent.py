import psutil
import requests
import time

SERVER_URL = "http://localhost:8082/api/report"

def monitor():
    print(f"--- Monitoring Started ---")
    print(f"Sending data to {SERVER_URL} every 5 seconds...")
    
    while True:
        try:
            # Gather Data
            cpu = psutil.cpu_percent(interval=1)
            ram = psutil.virtual_memory().available
            battery = psutil.sensors_battery()
            batt_level = battery.percent if battery else 100

            payload = {
                "cpuLoad": cpu,
                "freeRam": ram,
                "batteryLevel": batt_level
            }

            # Send to Java Server
            requests.post(SERVER_URL, json=payload)
            print(f"Sent: CPU {cpu}% | RAM {ram // (1024**3)}GB")

        except Exception as e:
            print(f"Error: {e}")
        
        time.sleep(5)

if __name__ == "__main__":
    monitor()