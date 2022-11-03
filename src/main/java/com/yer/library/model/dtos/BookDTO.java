package com.yer.library.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.yer.library.model.dtos.jsonviews.View;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.Year;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDTO {

    @JsonView(View.GetView.class)
    private Long id;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String isbn;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String title;

    @JsonView(View.PatchView.class)
    @NotBlank
    private Year year;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String author;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String type;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String genre;

    @JsonView(View.PatchView.class)
    @NotBlank
    private Integer value;
}
