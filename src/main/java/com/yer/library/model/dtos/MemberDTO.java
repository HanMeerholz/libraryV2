package com.yer.library.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.yer.library.model.dtos.jsonviews.View;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDTO {
    @JsonView(View.GetView.class)
    private Long id;

    @JsonView(View.PatchView.class)
    @NotBlank
    private Long membershipTypeId;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String name;

    @JsonView(View.PatchView.class)
    private String homeAddress;

    @JsonView(View.PatchView.class)
    @NotBlank
    private String emailAddress;

    @JsonView(View.PatchView.class)
    @NotBlank
    private LocalDate birthday;

    @JsonView(View.PatchView.class)
    @NotBlank
    private Long membershipId;
}
