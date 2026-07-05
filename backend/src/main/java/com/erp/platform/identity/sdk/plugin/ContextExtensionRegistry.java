package com.erp.platform.identity.sdk.plugin;

import com.erp.platform.identity.dto.RuntimeContext;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.function.Consumer;

@Component
public class ContextExtensionRegistry {

    private final Map<String, List<Consumer<RuntimeContext>>> enrichments = new LinkedHashMap<>();

    public void registerEnrichment(String extensionKey, Consumer<RuntimeContext> enricher) {
        enrichments.computeIfAbsent(extensionKey, k -> new ArrayList<>()).add(enricher);
    }

    public void applyEnrichments(RuntimeContext context) {
        enrichments.values().forEach(list -> list.forEach(e -> e.accept(context)));
    }

    public List<String> getRegisteredExtensions() {
        return List.copyOf(enrichments.keySet());
    }
}
