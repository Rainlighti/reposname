import json
import sys
import os

COMPILE_ERROR = 'COMPILE_ERROR'
JUDGE_ERROR = 'JUDGE_ERROR'
PASS = 'PASS'
REJECT = 'REJECT'
BEGIN_JUDGE = 'BEGIN_JUDGE'
BEGIN_COMPILE = 'BEGIN_COMPILE'
END_COMPILE = 'END_COMPILE'
END_JUDGE = 'END_JUDGE'
PENDING = 'PENDING'
DOWNLOAD_JUDGING_PROGRAM_ERROR = 'DOWNLOAD_JUDGING_PROGRAM_ERROR'
DOWNLOAD_USER_PROGRAM_ERROR = 'DOWNLOAD_USER_PROGRAM_ERROR'
WORKER_ERROR = 'WORKER_ERROR'

# debug info
WORKER_NAME = ""

# debug
OUTPUT_FILENAME = None

if len(sys.argv) >= 2:
    WORKER_NAME = sys.argv[1]


def make_result(
        result_type=WORKER_ERROR,
        test_point_id=-1,
        reason="",
        worker_name=WORKER_NAME):
    return {
        'resultType': result_type,
        'testPointId': test_point_id,
        'reason': reason,
        'workerName': worker_name,
    }


def output(result):
    if OUTPUT_FILENAME is None:
        sys.stdout.write(json.dumps(result) + "\n")
        sys.stdout.flush()
    else:
        try:
            with open(OUTPUT_FILENAME, "w+") as f:
                f.write(json.dumps(result) + "\n")
        except:
            # do nothing
            pass


# stop output to stdout
def toggle_output_to_file(filename="./score_system.log"):
    global OUTPUT_FILENAME
    OUTPUT_FILENAME = filename


def begin_compile():
    dir_path = os.path.dirname(os.path.realpath(__file__))
    # debug info
    output(make_result(BEGIN_COMPILE,
                       worker_name=WORKER_NAME,
                       reason=dir_path,
                       ))


def end_compile(warning):
    output(make_result(END_COMPILE, reason=warning))


def begin_judge():
    output(make_result(BEGIN_JUDGE))


def compile_error(reason=""):
    output(make_result(COMPILE_ERROR, reason=reason))


def judge_error(reason=""):
    output(make_result(JUDGE_ERROR, reason=reason))


def pending_testpoint(test_point_id):
    output(make_result(PENDING, test_point_id))


def pass_testpoint(test_point_id):
    output(make_result(PASS, test_point_id))


def reject_testpoint(test_point_id, reason="Wrong Answer"):
    output(make_result(REJECT, test_point_id, reason))


def end_judge():
    output(make_result(END_JUDGE))
