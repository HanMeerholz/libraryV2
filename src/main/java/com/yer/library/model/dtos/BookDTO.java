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
    Long id;

    @JsonView(View.PatchView.class)
    @NotBlank
    String isbn;

    @JsonView(View.PatchView.class)
    @NotBlank
    String title;

    @JsonView(View.PatchView.class)
    @NotBlank
    Year year;

    @JsonView(View.PatchView.class)
    @NotBlank
    String author;

    @JsonView(View.PatchView.class)
    @NotBlank
    String type;

    @JsonView(View.PatchView.class)
    @NotBlank
    String genre;

    @JsonView(View.PatchView.class)
    @NotBlank
    Integer value;
}
