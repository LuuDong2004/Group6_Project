package group6.cinema_project.controller.Admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import group6.cinema_project.dto.BranchDto;
import group6.cinema_project.dto.ScreeningRoomDto;
import group6.cinema_project.entity.ScreeningSchedule;
import group6.cinema_project.service.Admin.IAdminBranchService;
import group6.cinema_project.service.Admin.IAdminScreeningRoomService;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin/screening-rooms")
public class AdminScreeningRoomController {
        @Autowired
        private IAdminScreeningRoomService adminScreeningRoomService;

        @Autowired
        private IAdminBranchService adminBranchService;

    @GetMapping("/branch/{branchId}")
    public String listScreeningRooms(@PathVariable int branchId, Model model, RedirectAttributes redirectAttributes,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                     @RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "type", required = false) String type,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "rows", required = false) Integer rows,
                                     @RequestParam(value = "seatsPerRow", required = false) Integer seatsPerRow) {
        try {
            BranchDto branch = adminBranchService.findById(branchId);
            if (branch == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi nhánh!");
                return "redirect:/admin/branches";
            }

            Page<ScreeningRoomDto> roomPage = adminScreeningRoomService.getRoomsPage(branchId, page, size, name, type, status, rows, seatsPerRow);
            
            // Thêm thông tin về khả năng chỉnh sửa cho từng phòng
            List<ScreeningRoomDto> roomsWithEditInfo = roomPage.getContent().stream()
                .map(room -> {
                    boolean canEdit = adminScreeningRoomService.canEditRoom(room.getId());
                    room.setCanEdit(canEdit);
                    return room;
                })
                .collect(Collectors.toList());
            
            Page<ScreeningRoomDto> updatedRoomPage = new PageImpl<>(roomsWithEditInfo, roomPage.getPageable(), roomPage.getTotalElements());

            model.addAttribute("branch", branch);
            model.addAttribute("screeningRooms", updatedRoomPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", updatedRoomPage.getTotalPages());
            model.addAttribute("totalElements", updatedRoomPage.getTotalElements());
            model.addAttribute("screeningRoom", new ScreeningRoomDto());
            model.addAttribute("name", name);
            model.addAttribute("type", type);
            model.addAttribute("status", status);
            model.addAttribute("rows", rows);
            model.addAttribute("seatsPerRow", seatsPerRow);

            return "admin/admin_screening_room_management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải danh sách phòng chiếu: " + e.getMessage());
            return "redirect:/admin/branches";
        }
    }

    @GetMapping("/add")
    public String addScreeningRoomForm(@org.springframework.web.bind.annotation.RequestParam("branchId") int branchId, Model model) {
        BranchDto branch = adminBranchService.findById(branchId);
        ScreeningRoomDto screeningRoomDto = new ScreeningRoomDto();
        screeningRoomDto.setBranch(branch);
        screeningRoomDto.setRows(8);
        screeningRoomDto.setSeatsPerRow(8);
        screeningRoomDto.setStatus("ACTIVE");
        model.addAttribute("screeningRoom", screeningRoomDto);
        return "admin/admin_screening_room_edit";
    }

    @PostMapping("/add")
    public String addScreeningRoom(@Valid @ModelAttribute("screeningRoom") ScreeningRoomDto screeningRoomDto, BindingResult result,
                                   RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("screeningRoom", screeningRoomDto);
            return "admin/admin_screening_room_edit";
        }
//        if (adminScreeningRoomService.isNameDuplicate(screeningRoomDto.getName(), null)) {
//            result.rejectValue("name", "error.screeningRoom", "Tên phòng đã tồn tại.");
//            model.addAttribute("screeningRoom", screeningRoomDto);
//            return "admin/screening_room_edit";
//        }
        int rows = screeningRoomDto.getRows();
        int seats = screeningRoomDto.getSeatsPerRow();
        if (rows != seats) {
            redirectAttributes.addFlashAttribute("error", "Số hàng phải bằng số cột (hàng bằng cột)");
            return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
        }
        try {
            adminScreeningRoomService.saveOrUpdate(screeningRoomDto);
            redirectAttributes.addFlashAttribute("success", "Thêm phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm phòng chiếu: " + e.getMessage());
        }
        return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
    }

    @GetMapping("/edit/{id}")
    public String editScreeningRoomForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        ScreeningRoomDto screeningRoom = adminScreeningRoomService.getRoomById(id);
        if (screeningRoom == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu!");
            return "redirect:/admin/branches";
        }

        // Kiểm tra xem phòng có thể chỉnh sửa được không
        boolean canEdit = adminScreeningRoomService.canEditRoom(id);
        if (!canEdit) {
            List<ScreeningSchedule> activeSchedules = adminScreeningRoomService.getActiveSchedules(id);
            model.addAttribute("activeSchedules", activeSchedules);
            model.addAttribute("cannotEdit", true);
            model.addAttribute("screeningRoom", screeningRoom);
            return "admin/admin_screening_room_edit";
        }

        model.addAttribute("screeningRoom", screeningRoom);
        model.addAttribute("cannotEdit", false);
        model.addAttribute("canEdit", true);
        return "admin/admin_screening_room_edit";
    }

    @PostMapping("/edit/{id}")
    public String editScreeningRoom(@PathVariable int id, @Valid @ModelAttribute("screeningRoom") ScreeningRoomDto screeningRoomDto, BindingResult result,
                                    RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("screeningRoom", screeningRoomDto);
            return "admin/admin_screening_room_edit";
        }
//        if (adminScreeningRoomService.isNameDuplicate(screeningRoomDto.getName(), id)) {
//            result.rejectValue("name", "error.screeningRoom", "Tên phòng đã tồn tại.");
//            model.addAttribute("screeningRoom", screeningRoomDto);
//            return "admin/screening_room_edit";
//        }
        int rows = screeningRoomDto.getRows();
        int seats = screeningRoomDto.getSeatsPerRow();
        if (rows != seats) {
            redirectAttributes.addFlashAttribute("error", "Số hàng phải bằng số cột (hàng bằng cột)");
            return "redirect:/admin/screening-rooms/edit/" + id;
        }
        try {
            screeningRoomDto.setId(id);
            adminScreeningRoomService.saveOrUpdate(screeningRoomDto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật phòng chiếu: " + e.getMessage());
        }
        return "redirect:/admin/screening-rooms/branch/" + screeningRoomDto.getBranch().getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteScreeningRoom(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            ScreeningRoomDto screeningRoom = adminScreeningRoomService.getRoomById(id);
            if (screeningRoom == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu!");
                return "redirect:/admin/branches";
            }

            int branchId = screeningRoom.getBranch().getId();
            adminScreeningRoomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng chiếu thành công!");
            return "redirect:/admin/screening-rooms/branch/" + branchId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/branches";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa phòng chiếu. Phòng có thể đang được sử dụng.");
            return "redirect:/admin/branches";
        }
    }

    @GetMapping("/view/{id}")
    public String viewScreeningRoom(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        ScreeningRoomDto screeningRoom = adminScreeningRoomService.getRoomById(id);
        if (screeningRoom == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu!");
            return "redirect:/admin/branches";
        }
        
        // Kiểm tra xem phòng có thể chỉnh sửa được không
        boolean canEdit = adminScreeningRoomService.canEditRoom(id);
        if (!canEdit) {
            List<ScreeningSchedule> activeSchedules = adminScreeningRoomService.getActiveSchedules(id);
            model.addAttribute("activeSchedules", activeSchedules);
            model.addAttribute("readonly", true);
        } else {
            model.addAttribute("readonly", false);
        }
        
        model.addAttribute("screeningRoom", screeningRoom);
        // Luôn trả về template view, không phải edit
        return "admin/admin_screening_room_view";
    }
    }
