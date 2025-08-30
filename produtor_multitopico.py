from kafka import KafkaProducer
import json
import time
import random

# Configura o produtor do Kafka
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',  #endereço do seu broker Kafka
    value_serializer=lambda v: json.dumps(v).encode('utf-8'), #define como a mensagem é convertida em bytes antes de ser enviada. No nosso caso, estamos usando o json.dumps() para transformar um dicionário Python em uma string JSON e depois a codificando em utf-8.
    key_serializer=lambda k: str(k).encode('utf-8'),
    api_version=(0, 10, 1)
)

print("Produtor mult-tópico iniciado. Enviando dados de sensores...")

try:
    while True:
        # Envia dados de temperatura
        temp_device_id = f'temp_sensor_{random.randint(1, 3)}'
        temp_value = round(random.uniform(20.0, 30.0), 2)
        temp_message = {
            'device_id': temp_device_id,
            'value': temp_value,
            'unit': 'C',
            'timestamp': int(time.time())
        }
        producer.send('temperatura', key=temp_device_id, value=temp_message)
        print(f"Enviando para tópico 'temperatura': {temp_message}")

        # Envia dados de pressao
        press_device_id = f'press_sensor_{random.randint(1, 3)}'
        press_value = round(random.uniform(90.0, 110.0), 2)
        press_message = {
            'device_id': press_device_id,
            'value': press_value,
            'unit': 'kPa',
            'timestamp': int(time.time())
        }
        producer.send('pressao', key=press_device_id, value=press_message)
        print(f"Enviando para tópico 'pressao': {press_message}")

        time.sleep(1) # Envia um par de mensagens a cada segundo

except KeyboardInterrupt:
    print("Produtor mult-tópico finalizado.")
    producer.flush()
    producer.close()