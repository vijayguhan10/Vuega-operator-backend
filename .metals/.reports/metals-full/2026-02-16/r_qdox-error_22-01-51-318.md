error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[113,23]

error in qdox parser
file content:
```java
offset: 3528
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java
text:
```scala
package net.vuega.vuega_backend.Service.operator_config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.operator_config.OperatorConfigDTO;
import net.vuega.vuega_backend.Model.operator_config.OperatorConfig;
import net.vuega.vuega_backend.Repository.OperatorConfigRepository;

@Service
@RequiredArgsConstructor
public class OperatorConfigService {

    private final OperatorConfigRepository repository;

    // CREATE
    @Transactional
    public OperatorConfigDTO create(OperatorConfigDTO dto) {
        OperatorConfig entity = OperatorConfig.builder()
                .licenseKey(dto.getLicenseKey())
                .lastChecked(dto.getLastChecked() != null ? dto.getLastChecked() : LocalDateTime.now())
                .build();
        OperatorConfig saved = repository.save(entity);
        return toDTO(saved);
    }

    // READ ALL
    public List<OperatorConfigDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public Optional<OperatorConfigDTO> findById(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    // READ BY LICENSE KEY
    public Optional<OperatorConfigDTO> findByLicenseKey(String licenseKey) {
        return repository.findByLicenseKey(licenseKey).map(this::toDTO);
    }

    // UPDATE
    @Transactional
    public Optional<OperatorConfigDTO> update(Long id, OperatorConfigDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setLicenseKey(dto.getLicenseKey());
            existing.setLastChecked(dto.getLastChecked() != null ? dto.getLastChecked() : LocalDateTime.now());
            return toDTO(repository.save(existing));
        });
    }

    // PATCH (partial update)
    @Transactional
    public Optional<OperatorConfigDTO> patch(Long id, OperatorConfigDTO dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getLicenseKey() != null) {
                existing.setLicenseKey(dto.getLicenseKey());
            }
            if (dto.getLastChecked() != null) {
                existing.setLastChecked(dto.getLastChecked());
            }
            return toDTO(repository.save(existing));
        });
    }

    // DELETE BY ID
    @Transactional
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // DELETE ALL
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    // CHECK EXISTS BY ID
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    // CHECK EXISTS BY LICENSE KEY
    public boolean existsByLicenseKey(String licenseKey) {
        return repository.existsByLicenseKey(licenseKey);
    }

    // COUNT
    public long count() {
        return repository.count();
    }

    // UPDATE LAST CHECKED
    @Transactional
    public Optional<OperatorConfigDTO> updateLastChecked(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setLastChecked(LocalDateTime.now());
            return toDTO(repository.save(existing));
        });
    }
    System.out.println(@@"Updating last checked for ID: " + id);

    // HELPER: Entity to DTO
    private OperatorConfigDTO toDTO(OperatorConfig entity) {
        return OperatorConfigDTO.builder()
                .operatorId(entity.getOperatorId())
                .licenseKey(entity.getLicenseKey())
                .lastChecked(entity.getLastChecked())
                .build();
    }
}

```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.metals.SemanticdbDefinition$.foreachWithReturnMtags(SemanticdbDefinition.scala:99)
	scala.meta.internal.metals.Indexer.indexSourceFile(Indexer.scala:560)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3(Indexer.scala:691)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3$adapted(Indexer.scala:688)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.Indexer.reindexWorkspaceSources(Indexer.scala:688)
	scala.meta.internal.metals.MetalsLspService.$anonfun$onChange$2(MetalsLspService.scala:936)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:691)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:500)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

QDox parse error in file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java