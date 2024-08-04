package com.project.fstudy.common.cls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "format")
@NoArgsConstructor
public class BodyFormatter<T> {
    private T body;
    private String detail;
}
