package com.revoltcode.learnreactivespring.fluxandmono;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends Throwable{

    private String message;
}
