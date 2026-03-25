package com.mjdku.dkunotifier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Post {
    private String postSeq;
    private String title;
    private String author;
    private String date;
    private String url;
}
