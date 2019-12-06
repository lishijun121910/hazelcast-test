# hazelcast-test

enviroument:

JDK1.8

usage:

curl -d "maxSize=500000" "http://127.0.0.1:8080/ha/begin" 


hazelcast-server config:

hazelcast: 
  instance-name: hazelcast-node1
  group: 
    name: hazelcast-server
  network: 
    port: 
      port: 5701
      auto-increment: true
      port-count: 100
    join: 
      multicast: 
        enabled: false 
      tcp-ip: 
        enabled: true
        member-list: 
          - 10.11.117.39
          - 10.11.117.38
          - 10.11.117.32
  properties: 
    hazelcast.heartbeat.interval.seconds: 5
    hazelcast.master.confirmation.interval.seconds: 10
    hazelcast.max.join.seconds: 50
    hazelcast.max.no.heartbeat.seconds: 50
    hazelcast.max.no.master.confirmation.seconds: 70
    hazelcast.member.list.publish.interval.seconds: 50
    hazelcast.operation.call.timeout.millis: 5000
    hazelcast.merge.first.run.delay.seconds: 50
    hazelcast.merge.next.run.delay.seconds: 40
  map: 
    SimpleMap:
      in-memory-format: BINARY
      backup-count: 1
      async-backup-count: 0
      time-to-live-seconds: 0
      max-idle-seconds: 0
      eviction-policy: LRU
      max-size:
        policy: PER_NODE
        max-size: 0
      eviction-percentage: 25
      min-eviction-check-millis: 100
      merge-policy:
        batch-size: 100
        class-name: PutIfAbsentMergePolicy
      read-backup-data: false
