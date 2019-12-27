package com.neodem.orleans.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class PathTest {
    @Test
    void equalsShouldWorkForEqualPaths() {
        Path path1 = new Path(TokenLocation.Orleans, TokenLocation.Etampes, PathType.Land);
        Path path2 = new Path(TokenLocation.Orleans, TokenLocation.Etampes, PathType.Land);
        assertThat(path1).isEqualTo(path2);
        assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
    }

    @Test
    void hashShouldWorkForEqualPaths() {
        Path path1 = new Path(TokenLocation.Orleans, TokenLocation.Etampes, PathType.Land);
        Path path2 = new Path(TokenLocation.Orleans, TokenLocation.Etampes, PathType.Land);
        assertThat(path1).isEqualTo(path2);
        assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
    }

    @Test
    void pathsBetweenSamePlacesShouldBeEqual() {
        Path path1 = new Path(TokenLocation.Etampes, TokenLocation.Orleans, PathType.Land);
        Path path2 = new Path(TokenLocation.Orleans, TokenLocation.Etampes, PathType.Land);
        assertThat(path1).isEqualTo(path2);
    }

    @Test
    void pathsBetweenSamePlacesShouldHashTheSame() {
        Path path1 = new Path(TokenLocation.Etampes, TokenLocation.Orleans, PathType.Land);
        Path path2 = new Path(TokenLocation.Orleans, TokenLocation.Etampes, PathType.Land);
        assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
    }
}
