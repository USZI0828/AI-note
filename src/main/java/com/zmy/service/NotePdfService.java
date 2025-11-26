package com.zmy.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface NotePdfService {
    void exportNoteToPdf(Integer noteId, HttpServletResponse response) throws IOException;
}

