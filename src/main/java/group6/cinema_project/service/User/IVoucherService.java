package group6.cinema_project.service.User;

import group6.cinema_project.entity.Qa.Voucher;

public interface IVoucherService {
    Voucher validateVoucher(String code);
    void markVoucherUsed(Long id);
}
