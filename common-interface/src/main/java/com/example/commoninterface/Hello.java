package com.example.commoninterface;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Hello implements Serializable {
    private String message="123";
    private String description="123";
}
