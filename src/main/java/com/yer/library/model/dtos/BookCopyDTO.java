package com.yer.library.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.yer.library.model.Location;
import com.yer.library.model.dtos.jsonviews.View;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookCopyDTO {
    @JsonView(View.GetView.class)
    Long id;

    @JsonView(View.PatchView.class)
    @NotBlank
    Long bookId;

    @JsonView(View.PatchView.class)
    @NotBlank
    Location location;
}
