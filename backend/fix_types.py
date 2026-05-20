import os

base_dir = r"c:\Users\saki\Desktop\OOP-Group_Project\backend\src\main\java\com\primeestate"

for root, _, files in os.walk(base_dir):
    for f in files:
        if f.endswith(".java"):
            path = os.path.join(root, f)
            with open(path, "r") as file:
                content = file.read()
            
            # Replace Long with Integer
            content = content.replace("Long id", "Integer id")
            content = content.replace("Long getId", "Integer getId")
            content = content.replace("setId(Long", "setId(Integer")
            content = content.replace("JpaRepository<User, Long>", "JpaRepository<User, Integer>")
            content = content.replace("JpaRepository<Agent, Long>", "JpaRepository<Agent, Integer>")
            content = content.replace("JpaRepository<Property, Long>", "JpaRepository<Property, Integer>")
            content = content.replace("JpaRepository<Appointment, Long>", "JpaRepository<Appointment, Integer>")
            content = content.replace("getUserProfile(Long", "getUserProfile(Integer")
            content = content.replace("updateUser(Long", "updateUser(Integer")
            content = content.replace("deactivateUser(Long", "deactivateUser(Integer")
            content = content.replace("@PathVariable Long", "@PathVariable Integer")
            
            with open(path, "w") as file:
                file.write(content)
