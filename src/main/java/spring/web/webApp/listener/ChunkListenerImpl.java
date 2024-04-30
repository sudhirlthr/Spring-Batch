package spring.web.webApp.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;

public class ChunkListenerImpl {

    @BeforeChunk
    public void beforeChunk(){
        System.out.println(">>before Chunk listener");
    }

    @AfterChunk
    public void afterChunk(){
        System.out.println("<<After Chunk listener");
    }
}
