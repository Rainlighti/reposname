import os
from .score_system import begin_judge, \
    begin_compile, \
    end_judge, \
    pass_testpoint, \
    pending_testpoint, \
    reject_testpoint, \
    compile_error, \
    runtime_error

begin_compile()
# compile_error(compile_msg)
begin_judge()
# runtime_error(qemu_msg)
test_point_id_list = [1, 2, 3]
for id in test_point_id_list:
    pending_testpoint(id)
    # runtime_error(reason=reason, test_point_id=id)
    pass_testpoint(id)
    # reject_testpoint(id)
end_judge()
