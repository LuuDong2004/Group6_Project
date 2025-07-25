package group6.cinema_project.service.User.Impl;

import group6.cinema_project.entity.Qa.Voucher;
import group6.cinema_project.repository.User.VoucherRepository;
import group6.cinema_project.service.User.IVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements IVoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public Voucher validateVoucher(String code) {
        return voucherRepository.findValidCode(code, LocalDate.now()).orElse(null);
    }
    @Override
    public void markVoucherUsed(Long id) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isPresent()) {
            Voucher voucher = optionalVoucher.get();
            voucher.setStatus("USED");
            voucherRepository.save(voucher);
        }
    }
} 