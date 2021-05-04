import pathlib
def log(msg):
    if pathlib.Path("action_logs.txt").exists:
        with open("action_logs.txt", "a") as file:
            file.write("\n"+msg)
    else:
        with open("action_logs.txt", "w") as file:
            file.write(msg)
log("first message")
log("second message")
