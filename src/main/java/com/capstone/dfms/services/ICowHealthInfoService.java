package com.capstone.dfms.services;

import com.capstone.dfms.responses.CowHealthInfoResponse;

import java.util.List;

public interface ICowHealthInfoService {
    List<CowHealthInfoResponse<?>> getAllHealthInfoOrderedDesc(Long cowId);
}
