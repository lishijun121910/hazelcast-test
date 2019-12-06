# hazelcast-test

enviroument:

JDK1.8

usage:

curl -d "maxSize=500000" "http://127.0.0.1:8080/ha/begin" 


hazelcast-server config:

hazelcast:
  2   instance-name: hazelcast-node1
  3   group:
  4     name: hazelcast-server
  5   network:
  6     port:
  7       port: 5701
  8       auto-increment: true
  9       port-count: 100
 10     join:
 11       multicast:
 12         enabled: false
 13       tcp-ip:
 14         enabled: true
 15         member-list:
 16           - 10.11.117.39
 17           - 10.11.117.38
 18           - 10.11.117.32
 19   properties:
 20 #    hazelcast.io.thread.count: 6
 21     hazelcast.heartbeat.interval.seconds: 5
 22     hazelcast.master.confirmation.interval.seconds: 10
 23     hazelcast.max.join.seconds: 50
 24     hazelcast.max.no.heartbeat.seconds: 50
 25     hazelcast.max.no.master.confirmation.seconds: 70
 26     hazelcast.member.list.publish.interval.seconds: 50
 27     hazelcast.operation.call.timeout.millis: 5000
 28     hazelcast.merge.first.run.delay.seconds: 50
 29     hazelcast.merge.next.run.delay.seconds: 40
 30   map:
 31     SimpleMap:
 32       in-memory-format: BINARY
 33       backup-count: 1
 34       async-backup-count: 0
 35       time-to-live-seconds: 0
 36       max-idle-seconds: 0
 37       eviction-policy: LRU
 38       max-size:
 39         policy: PER_NODE
 40         max-size: 0
 41       eviction-percentage: 25
 42       min-eviction-check-millis: 100
 43       merge-policy:
 44         batch-size: 100
 45         class-name: PutIfAbsentMergePolicy
 46       read-backup-data: false

