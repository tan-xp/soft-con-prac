package com.softcon.service;

import com.softcon.entity.Paper;

import java.util.List;

public interface PaperService {
    List<Paper> getAllPapers();
    Paper getPaperById(Integer id);
    boolean addPaper(Paper paper);
    boolean updatePaper(Paper paper);
    boolean deletePaper(Integer id);
}