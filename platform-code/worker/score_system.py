import json
import sys
import os

COMPILE_ERROR = 'COMPILE_ERROR'
RUNTIME_ERROR = 'RUNTIME_ERROR'
PASS = 'PASS'
REJECT = 'REJECT'
BEGIN_JUDGE = 'BEGIN_JUDGE'
BEGIN_COMPILE = 'BEGIN_COMPILE'
END_JUDGE = 'END_JUDGE'
PENDING = 'PENDING'
DOWNLOAD_JUDGING_PROGRAM_ERROR = 'DOWNLOAD_JUDGING_PROGRAM_ERROR'
DOWNLOAD_USER_PROGRAM_ERROR = 'DOWNLOAD_USER_PROGRAM_ERROR'
WORKER_ERROR = 'WORKER_ERROR'

# debug info
WORKER_NAME = ""
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
    sys.stdout.write(json.dumps(result))
    sys.stdout.flush()


def begin_compile():
    dir_path = os.path.dirname(os.path.realpath(__file__))
    # debug info
    output(make_result(BEGIN_COMPILE,
                       worker_name=WORKER_NAME,
                       reason=dir_path,
                       ))


def begin_judge():
    output(make_result(BEGIN_JUDGE))


def compile_error(reason=""):
    output(make_result(COMPILE_ERROR, reason=reason))


def runtime_error(reason="", test_point_id=-1):
    output(make_result(RUNTIME_ERROR, reason=reason, test_point_id=test_point_id))


def pending_testpoint(test_point_id):
    output(make_result(PENDING, test_point_id))


def pass_testpoint(test_point_id):
    output(make_result(PASS, test_point_id))


def reject_testpoint(test_point_id):
    output(make_result(REJECT, test_point_id))


def end_judge():
    output(make_result(END_JUDGE))
