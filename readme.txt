java -jar ./build/libs/elk-0.0.1-SNAPSHOT.jar --server.port=8080
python3 asynchronous_requests.py

http://es_url/_cat/thread_pool?v&h=node_name,name,size,queue,queue_size,rejected,completed,min,max


