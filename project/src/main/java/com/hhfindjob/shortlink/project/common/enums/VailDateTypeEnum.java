package com.hhfindjob.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VailDateTypeEnum {

    /**
     *  永久有效期
     */
    FOREVER(0),

    /**
     *
     */
    NOTFOREVER(1);

    @Getter
    private final int type;

}
