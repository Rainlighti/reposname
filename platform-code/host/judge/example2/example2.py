import time
import random
import subprocess

from score_system import begin_judge, \
    begin_compile, \
    end_compile, \
    end_judge, \
    pass_testpoint, \
    pending_testpoint, \
    reject_testpoint, \
    toggle_output_to_file, \
    compile_error, \
    judge_error

time.sleep(4)
test_point_ids = [3558,3559,3560]

# toggle_output_to_file()
begin_compile()
time.sleep(4)
p = subprocess.Popen("g++ ./main.cpp -o main",
                     shell=True,
                     stderr=subprocess.PIPE,
                     stdout=subprocess.PIPE
                     )
error = [bytes.decode(line) for line in p.stderr.readlines()]
warning = [bytes.decode(line) for line in p.stdout.readlines()]
if len(error) != 0:
    compile_error(error)
if len(warning) != 0:
    end_compile(warning=warning)
else:
    end_compile(warning="No warning")
begin_judge()

time.sleep(3)


# runtime_error(qemu_msg)
def test_point_1(id):
    pending_testpoint(id)
    time.sleep(3)
    user_code = subprocess.Popen("./main",
                                 shell=True,
                                 stdout=subprocess.PIPE,
                                 stdin=subprocess.PIPE
                                 )
    user_code.stdin.write("1 2\n".encode())
    user_code.stdin.flush()
    output = bytes.decode(user_code.stdout.readline())
    if output == "3\n":
        pass_testpoint(id)
    else:
        reject_testpoint(id, "Wrong Answer")


def test_point_2(id):
    pending_testpoint(id)
    time.sleep(3)
    user_code = subprocess.Popen("./main",
                                 shell=True,
                                 stdout=subprocess.PIPE,
                                 stdin=subprocess.PIPE
                                 )

    user_code.stdin.write("123 456\n".encode())
    user_code.stdin.flush()
    output = bytes.decode(user_code.stdout.readline())
    if output == "579\n":
        pass_testpoint(id)
    else:
        reject_testpoint(id, "Wrong Answer")


def test_point_3(id):
    pending_testpoint(id)
    time.sleep(3)
    user_code = subprocess.Popen("./main",
                                 shell=True,
                                 stdout=subprocess.PIPE,
                                 stdin=subprocess.PIPE
                                 )
    big_int_1 = 0x0fffffff
    big_int_2 = 0x80000000
    user_code.stdin.write(str(big_int_1).encode());
    user_code.stdin.write(" ".encode());
    user_code.stdin.write(str(big_int_2).encode());
    user_code.stdin.write("\n".encode());
    user_code.stdin.flush()
    output = bytes.decode(user_code.stdout.readline())
    if output == "2415919103\n":
        pass_testpoint(id)
    else:
        reject_testpoint(id, "Wrong Answer")


test_functions = [test_point_1, test_point_2, test_point_3]
for (id, func) in zip(test_point_ids, test_functions):
    func(id)

time.sleep(3)
end_judge()
