package com.softcon.service.impl;

import com.softcon.entity.Paper;
import com.softcon.mapper.PaperMapper;
import com.softcon.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Override
    public List<Paper> getAllPapers() {
        return paperMapper.getAllPapers();
    }

    @Override
    public Paper getPaperById(Integer id) {
        return paperMapper.getPaperById(id);
    }

    @Override
    public boolean addPaper(Paper paper) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        paper.setCreatedAt(df.format(new Date()));
        return paperMapper.insert(paper) > 0;
    }

    @Override
    public boolean updatePaper(Paper paper) {
        return paperMapper.update(paper) > 0;
    }

    @Override
    public boolean deletePaper(Integer id) {
        return paperMapper.delete(id) > 0;
    }
}