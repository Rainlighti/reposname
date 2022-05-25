import time
import random

from score_system import begin_judge, \
    begin_compile, \
    end_compile, \
    end_judge, \
    pass_testpoint, \
    pending_testpoint, \
    reject_testpoint, \
    compile_error, \
    judge_error

time.sleep(5)
begin_compile()
time.sleep(5)
# compile_error(compile_msg)
end_compile(warning="No warning")
begin_judge()
time.sleep(3)
# runtime_error(qemu_msg)
test_point_id_list = [9, 10, 11]
for id in test_point_id_list:
    pending_testpoint(id)
    time.sleep(3)
    # judge_error(reason=reason)
    if random.randint(0, 1) == 1:
        pass_testpoint(id)
    else:
        reject_testpoint(id, "Bad luck so this test point did not pass")
time.sleep(3)
end_judge()
