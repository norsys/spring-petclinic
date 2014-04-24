package org.springframework.samples.petclinic.model;

import lombok.Data;

@Data
public class Comment {
    private String pseudo;
    private String comment;
    private String date;
    private int rate;
}
