package com.sky.AgentCore.dto.rag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
@Data
public class RerankRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1272322443949808505L;

    private String model;

    private String query;

    private List<String> documents;

    @JsonProperty("return_documents")
    private boolean returnDocuments = false;

    @JsonProperty("max_chunks_per_doc")
    private Integer maxChucksPerDoc = 10;

    @JsonProperty("overlap_tokens")
    private Integer overlapTokens = 80;

}
