error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/CreateSeatRequest.java
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/CreateSeatRequest.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[1,49]

error in qdox parser
file content:
```java
offset: 49
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/CreateSeatRequest.java
text:
```scala
package net.vuega.vuega_backend.DTO.seats.seat;\n@@\nimport java.math.BigDecimal;\n\nimport jakarta.validation.constraints.DecimalMin;\nimport jakarta.validation.constraints.NotBlank;\nimport jakarta.validation.constraints.NotNull;\nimport jakarta.validation.constraints.Size;\nimport lombok.AllArgsConstructor;\nimport lombok.Builder;\nimport lombok.Data;\nimport lombok.NoArgsConstructor;\nimport net.vuega.vuega_backend.Model.seats.seat.SeatType;\n\n@Data\n@NoArgsConstructor\n@AllArgsConstructor\n@Builder\npublic class CreateSeatRequest {\n\n    @NotNull(message = "busId is required")\n    private Long busId;\n\n    @NotBlank(message = "seatNo is required")\n    @Size(max = 10, message = "seatNo must be at most 10 characters")\n    private String seatNo;\n\n    @NotNull(message = "type is required (SEATER or SLEEPER)")\n    private SeatType type;\n\n    @NotNull(message = "basePrice is required")\n    @DecimalMin(value = "0.01", message = "basePrice must be greater than 0")\n    private BigDecimal basePrice;\n}

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

QDox parse error in file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/seats/seat/CreateSeatRequest.java