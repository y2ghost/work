package study.ywork.web.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/test-emitter")
public class EmitterController {
    @GetMapping
    public ResponseBodyEmitter handleEmitter() {
        final ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    emitter.send(new BigDecimal(i));
                    emitter.send(" - ", MediaType.TEXT_PLAIN);
                    Thread.sleep(10);
                } catch (Exception e) {
                    System.err.println(e);
                    emitter.completeWithError(e);
                    return;
                }
            }

            emitter.complete();
        });

        return emitter;
    }

    @GetMapping("/sse")
    public ResponseBodyEmitter handleSSEEmitter() {
        final SseEmitter emitter = new SseEmitter();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    String chunk = String.format("%d: %s", i, LocalTime.now());
                    emitter.send(chunk, MediaType.TEXT_PLAIN);
                    Thread.sleep(200);
                } catch (Exception e) {
                    System.err.println(e);
                    emitter.completeWithError(e);
                    return;
                }
            }

            emitter.complete();
        });

        return emitter;
    }

    @GetMapping("/raw")
    public StreamingResponseBody handleRaw() {
        return new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream out) throws IOException {
                for (int i = 90; i < 100; i++) {
                    out.write((Integer.toString(i) + " - ").getBytes());
                    out.flush();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        System.err.println(e);
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
    }

    @GetMapping(value = "/write-object", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody writeObject() {
        return outputStream -> {
            Map<String, BigInteger> map = new HashMap<>();
            map.put("one", BigInteger.ONE);
            map.put("ten", BigInteger.TEN);
            try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
                oos.writeObject(map);
            }
        };
    }
}
