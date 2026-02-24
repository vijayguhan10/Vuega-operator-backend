error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/ResponseDto.java:_empty_/ResponseDto#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/ResponseDto.java
empty definition using pc, found symbol in pc: _empty_/ResponseDto#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 212
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/DTO/ResponseDto.java
text:
```scala
package net.vuega.vuega_backend.DTO;

// Generic API response wrapper used by all endpoints.
@Data

private int status;private String message;private T data;

public static<T>ResponseDto<T>success(T data){return @@ResponseDto.<T>builder().status(200).message("Success").data(data).build();}

public static<T>ResponseDto<T>created(T data){return ResponseDto.<T>builder().status(201).message("Created").data(data).build();}

public static<T>ResponseDto<T>notFound(String message){return ResponseDto.<T>builder().status(404).message(message).data(null).build();}

public static<T>ResponseDto<T>error(int status,String message){return ResponseDto.<T>builder().status(status).message(message).data(null).build();}}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/ResponseDto#