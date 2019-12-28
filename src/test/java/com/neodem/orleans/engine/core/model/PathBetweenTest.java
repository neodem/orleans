package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.engine.core.model.PathBetween;
import com.neodem.orleans.engine.core.model.TokenLocation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class PathBetweenTest {

    @Test
    void equalsShouldWorkForSamePathsBetween() {
        PathBetween pb1 = new PathBetween(TokenLocation.Tours, TokenLocation.Orleans);
        PathBetween pb2 = new PathBetween(TokenLocation.Tours, TokenLocation.Orleans);
        assertThat(pb1).isEqualTo(pb2);
    }

    @Test
    void equalsShouldWorkForSamePathsBetweenDiffOrder() {
        PathBetween pb1 = new PathBetween(TokenLocation.Orleans, TokenLocation.Tours);
        PathBetween pb2 = new PathBetween(TokenLocation.Tours, TokenLocation.Orleans);
        assertThat(pb1).isEqualTo(pb2);
    }

    @Test
    void hashCodeShouldWorkForSamePathsBetween() {
        PathBetween pb1 = new PathBetween(TokenLocation.Tours, TokenLocation.Orleans);
        PathBetween pb2 = new PathBetween(TokenLocation.Tours, TokenLocation.Orleans);
        assertThat(pb1.hashCode()).isEqualTo(pb2.hashCode());
    }

    @Test
    void hashCodeShouldWorkForSamePathsBetweenDiffOrder() {
        PathBetween pb1 = new PathBetween(TokenLocation.Orleans, TokenLocation.Tours);
        PathBetween pb2 = new PathBetween(TokenLocation.Tours, TokenLocation.Orleans);
        assertThat(pb1.hashCode()).isEqualTo(pb2.hashCode());
    }
}
