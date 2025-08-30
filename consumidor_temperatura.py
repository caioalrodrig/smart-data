from kafka import KafkaConsumer
import json
import time

# Configura o consumidor para o tópico 'temperatura'
consumer = KafkaConsumer(
    'temperatura',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id='grupo_temperatura',
    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
    api_version=(0, 10, 1)
)

print("Consumidor de temperatura iniciado. Aguardando dados...")

try:
    for message in consumer:
        data = message.value
        print(f"[{time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(data['timestamp']))}] "
              f"TOPICO: {message.topic} | DISPOSITIVO: {data['device_id']} | "
              f"VALOR: {data['value']}{data['unit']}")
except KeyboardInterrupt:
    print("Consumidor de temperatura finalizado.")
    consumer.close()