package com.github.kinetic.tracething.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public record Resource(String domain, String path) {

    /**
     * Read a resource to string
     *
     * @return the contents of resource as string
     */
    public String read() {
        final String resourcePath = domain + "/" + path;
        final InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        final byte[] content;

        System.out.println(resourcePath);

        try(in) {
            assert in != null;

            content = in.readAllBytes();
        } catch(final IOException ioException) {
            return null;
        }

        return new String(content, StandardCharsets.UTF_8);
    }

    /**
     * Check if a resource exists
     *
     * @return true if resource exists, false if it does not
     */
    public boolean exists() {
        final String resourcePath = domain + path;
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if(inputStream == null)
            return false;

        try {
            inputStream.close();

            return true;
        } catch(final IOException ioException) {
            return false;
        }
    }

    /**
     * Read the resource to bytes
     *
     * @return reads resource to bytes
     */
    public byte[] readBytes() {
        final String resourcePath = domain + path;
        final InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);

        try(in) {
            assert in != null;

            return in.readAllBytes();
        } catch(final IOException ioException) {
            return null;
        }
    }

    /**
     * Get full file to resource
     *
     * @return domain and file combined (full file)
     */
    public String getFullPath() {
        return domain + path;
    }
}
