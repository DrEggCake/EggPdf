package com.dreggcake.src.renderer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Shader {

    public int ID;


    public Shader(ShaderSource... sources) {

        if (sources.length == 0) {
            throw new IllegalArgumentException(
                    "Shader requires at least one source."
            );
        }

        ID = GL20.glCreateProgram();

        List<Integer> compiledShaders = new ArrayList<>();

        for (ShaderSource source : sources) {

            String code = loadResource(source.path);

            int shader = GL20.glCreateShader(source.type.glType);

            GL20.glShaderSource(shader, code);
            GL20.glCompileShader(shader);

            int success = GL20.glGetShaderi(
                    shader,
                    GL20.GL_COMPILE_STATUS
            );

            if (success == GL20.GL_FALSE) {

                String infoLog = GL20.glGetShaderInfoLog(shader);

                throw new RuntimeException(
                        "ERROR::SHADER::"
                                + source.type.name()
                                + "::COMPILATION_FAILED\n"
                                + infoLog
                );
            }

            GL20.glAttachShader(ID, shader);

            compiledShaders.add(shader);
        }

        GL20.glLinkProgram(ID);

        int success = GL20.glGetProgrami(
                ID,
                GL20.GL_LINK_STATUS
        );

        if (success == GL20.GL_FALSE) {

            String infoLog = GL20.glGetProgramInfoLog(ID);

            throw new RuntimeException(
                    "ERROR::SHADER::PROGRAM::LINKING_FAILED\n"
                            + infoLog
            );
        }

        // Cleanup
        for (int shader : compiledShaders) {
            GL20.glDeleteShader(shader);
        }
    }


    private static String loadResource(String path) {

        try (InputStream is =
                     Shader.class.getResourceAsStream(path)) {

            if (is == null) {
                throw new RuntimeException(
                        "Resource not found: " + path
                );
            }

            return new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load resource: " + path,
                    e
            );
        }
    }



    public void use() {
        GL20.glUseProgram(ID);
    }

    public void delete() {
        GL20.glDeleteProgram(ID);
    }

    public void setBool(String name, boolean value) {
        GL20.glUniform1i(
                getUniformLocation(name),
                value ? 1 : 0
        );
    }

    public void setInt(String name, int value) {
        GL20.glUniform1i(
                getUniformLocation(name),
                value
        );
    }

    public void setFloat(String name, float value) {
        GL20.glUniform1f(
                getUniformLocation(name),
                value
        );
    }

    private int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(ID, name);
    }

    // not a record class for readability to me
    public static class ShaderSource {

        public final ShaderType type;
        public final String path;

        public ShaderSource(
                ShaderType type,
                String path
        ) {
            this.type = type;
            this.path = path;
        }
    }



    public enum ShaderType {

        VERTEX(GL20.GL_VERTEX_SHADER),
        FRAGMENT(GL20.GL_FRAGMENT_SHADER),
        GEOMETRY(GL32.GL_GEOMETRY_SHADER),
        TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
        TESS_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER),
        COMPUTE(GL43.GL_COMPUTE_SHADER);

        public final int glType;

        ShaderType(int glType) {
            this.glType = glType;
        }
    }
}