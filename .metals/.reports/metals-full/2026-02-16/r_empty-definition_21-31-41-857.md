error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/operator_config/OperatorConfig.java:_empty_/AllArgsConstructor#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/operator_config/OperatorConfig.java
empty definition using pc, found symbol in pc: _empty_/AllArgsConstructor#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 206
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Model/operator_config/OperatorConfig.java
text:
```scala
package net.vuega.vuega_backend.Model.operator_config;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operator_config")
@Data
@NoArgsConstructor
@All@@ArgsConstructor
@Builder
public class OperatorConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "license_key", nullable = false)
    private String licenseKey;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/AllArgsConstructor#