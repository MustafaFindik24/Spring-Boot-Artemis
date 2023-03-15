# Apache Artemis Nedir?

![image](https://user-images.githubusercontent.com/91599453/225305645-d9305a5e-00f2-4948-8237-cd2aa12d943a.png)

Apache Artemis; açık kaynaklı, ölçeklenebilen, API desteği sunan, JMS (Java Message Service) ile entegre çalışabilen bir mesajlaşma sistemidir.


# Spring Boot projesinde Artemis kullanımı

* Artemisi kullanmak için Spring Boot projemizin içerisinde bulunan pom.xml dosyamıza dependency sini ekliyoruz.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-artemis</artifactId>
</dependency>
```

* Artemisi Docker üzerinde çalıştıracağız. Bu yüzden docker-compose dosyasını oluşturup Apache Artemis imajından container ayağa kaldıracağız.

```yml
version: '3.1'
services:
  artemis:
    image: vromero/activemq-artemis:2.15.0
    container_name: artemis

    restart: always

    environment:
      - "TZ"
      - "ARTEMIS_USERNAME=admin"
      - "ARTEMIS_PASSWORD=password"
    ports:
      - "8161:8161"
      - "61616:61616"

    volumes:
      - "./data/artemis-data:/var/lib/artemis/data:Z"
```

* Bir service package ı altında gönderen ve alıcı olarak iki farklı service oluşturduk. DispatcherService servisinin içinde Spring Framework'de bulunan, gönderim işlemlerini sağlaması için JmsTemplate sınıfını servisimize inject ettik. Artemis'de mesajı göndereceğimiz bir kuyruk için değişken oluşturduk ve metotları tanımladık.

```java
@Service
public class DispatcherService {
    private final JmsTemplate jmsTemplate; // Jms'ye mesaj göndermekten sorumlu
    public DispatcherService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    @Value("${jms.queue}")
    String queue;//Mesajı göndereceğimiz bir destination queue
    @Value("Q.object")
    String objqueue;

    public void sendTheMessage(String message){
        jmsTemplate.convertAndSend(queue,message);
    }
    public void sendObject(Message objmessage){
        jmsTemplate.convertAndSend(objqueue,objmessage);
    }
}
```

* ReceiverService isimli serviste ise @JmsListener kullanarak kuyruktaki mesajın Slf4j kullanarak projemizde log olarak bastırılmasını sağladık.

```java
@Service
public class ReceiverService {
    Logger logger = LoggerFactory.getLogger(ReceiverService.class);
    @JmsListener(destination = "${jms.queue}")
    public void receiverMessage(String message){
        logger.info("Received message is :" +message);
    }
    @JmsListener(destination = "Q.object")
    public void receiverObject(Message objmessage){
        logger.info("Received object message is : " + objmessage);
    }
}
```

* Configuration sınıfı oluşturarak gelen verinin text formatında set edilmesini sağladık.

```java
@Configuration
public class JmsConfig {
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
```

* Son olarak controller sınıfımızda ise DispatcherService sınıfını inject edip post isteğinde bulunduk.

```java
@RestController
@RequestMapping("/message")
public class MessageController {
    private final DispatcherService dispatcherService;

    public MessageController(DispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }
    @PostMapping
    public ResponseEntity send(@RequestBody String message){
        dispatcherService.sendTheMessage(message);
        return ResponseEntity.ok("Message send : " + message);
    }
    @PostMapping("/object")
    public ResponseEntity sendObject(@RequestBody Message objmessage){
        dispatcherService.sendObject(objmessage);
        return ResponseEntity.ok("Message send : " + objmessage);
    }
}
```

* Docker-compose dosyasını çalıştırıp projemizi ayağa kaldırdıktan sonra 
