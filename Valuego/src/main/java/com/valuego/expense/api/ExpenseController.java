package com.valuego.expense.api;

import com.valuego.expense.api.dto.request.ExpenseReqDto;
import com.valuego.expense.api.dto.response.ExpenseInfoResDto;
import com.valuego.expense.api.dto.response.ExpenseListResDto;
import com.valuego.expense.service.ExpenseService;
import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expense API", description = "지출 API")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 기록 생성", description = "그룹에 포함된 사용자가 지출 기록을 생성합니다.")
    @PostMapping
    public ApiResTemplate<ExpenseInfoResDto> createExpense(Principal principal,
                                                           @Valid @RequestBody ExpenseReqDto expenseReqDto,
                                                           @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        ExpenseInfoResDto expenseInfoResDto = expenseService.createExpense(principal, expenseReqDto, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, expenseInfoResDto);
    }

    @Operation(summary = "지출 기록 전체 조회", description = "그룹에 포함된 사용자가 지출 기록 리스트를 조회합니다.")
    @GetMapping("/all")
    public ApiResTemplate<ExpenseListResDto> getAllExpenses(Principal principal,
                                                            @RequestParam Long groupId,
                                                            @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        ExpenseListResDto expenseListResDto = expenseService.getAllExpenses(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, expenseListResDto);
    }
}
