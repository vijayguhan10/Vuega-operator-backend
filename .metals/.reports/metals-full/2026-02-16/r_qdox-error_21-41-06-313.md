error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[83,23]

error in qdox parser
file content:
```java
offset: 3011
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java
text:
```scala
package net.vuega.vuega_backend.Controller.OperatorConfig;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.operator_config.OperatorConfigDTO;
import net.vuega.vuega_backend.Service.operator_config.OperatorConfigService;

@RestController
@RequestMapping("/api/operator-config")
@RequiredArgsConstructor
public class OperatorConfigController {

    private final OperatorConfigService service;

    // CREATE
    @PostMapping
    public ResponseEntity<OperatorConfigDTO> create(@RequestBody OperatorConfigDTO dto) {
        OperatorConfigDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<OperatorConfigDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OperatorConfigDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ BY LICENSE KEY
    @GetMapping("/license/{licenseKey}")
    public ResponseEntity<OperatorConfigDTO> findByLicenseKey(@PathVariable String licenseKey) {
        return service.findByLicenseKey(licenseKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE (full)
    @PutMapping("/{id}")
    public ResponseEntity<OperatorConfigDTO> update(@PathVariable Long id, @RequestBody OperatorConfigDTO dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH (partial)
    @PatchMapping("/{id}")
    public ResponseEntity<OperatorConfigDTO> patch(@PathVariable Long id, @RequestBody OperatorConfigDTO dto) {
        return service.patch(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE BY ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE ALL
    System.out.println(@@"OperatorConfigController class loaded");
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }

    
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(service.existsById(id));
    }

    // CHECK EXISTS BY LICENSE KEY
    @GetMapping("/exists/license/{licenseKey}")
    public ResponseEntity<Boolean> existsByLicenseKey(@PathVariable String licenseKey) {
        return ResponseEntity.ok(service.existsByLicenseKey(licenseKey));
    }

    // COUNT
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    // UPDATE LAST CHECKED (validate license)
    @PostMapping("/{id}/validate")
    public ResponseEntity<OperatorConfigDTO> updateLastChecked(@PathVariable Long id) {
        return service.updateLastChecked(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

QDox parse error in file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java