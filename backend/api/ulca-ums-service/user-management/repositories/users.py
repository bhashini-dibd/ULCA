from models import UserManagementModel
from models import post_error
from utilities import UserUtils
import time
import datetime

userModel   =   UserManagementModel()

class UserManagementRepositories:
    
    def create_users(self,users):

        records                         =   []
        for user in users:
            users_data                  =   {}
            hashed                      =   UserUtils.hash_password(user["password"])
            user_id                     =   UserUtils.generate_user_id()

            users_data["userID"]        =   user_id
            users_data["email"]         =   user["email"]
            users_data["firstName"]     =   user["firstName"]
            users_data["password"]      =   hashed.decode("utf-8")
            
            if "lastName" in user:
                users_data["lastName"]  =   user["lastName"]
            if "phoneNo" in user:
                users_data["phoneNo"]   =   user["phoneNo"]
            if "roles" in user:
                users_data["roles"]     =   user["roles"]
                
            users_data["isVerified"]   =   False
            users_data["isActive"]     =   False
            users_data["registeredTime"]   =   datetime.datetime.utcnow()
            users_data["activatedTime"]    =   0
            records.append(users_data)
        if not records:
            return post_error("Data Null", "Data recieved for user creation is empty", None)

        result = userModel.create_users(records)
        if result is not None:
            return result

    def update_users(self,users):
        records                         =   []
        for user in users:
            users_data                  =   {}
            users_data["userID"]        =   user["userID"]
            if user.get("name")         !=  None:
                users_data["name"]      =   user["name"]
            if user.get("email")        !=  None:
                users_data["email"]     =   user["email"]
            if user.get("phoneNo")      !=  None:
                users_data["phoneNo"]   =   user["phoneNo"]
            if user.get("description")      !=  None:
                users_data["description"]    =  user["description"]
            if user.get("roles_new")        !=  None:
                users_data["roles"]          =  user["roles_new"]
            records.append(users_data)

        result = userModel.update_users_by_uid(records)
        if result is not None:
            return result
        else:
            return True

    def search_users(self,user_ids, user_names, role_codes,org_codes,offset,limit_value,skip_pagination):
        result = userModel.get_user_by_keys(
            user_ids, user_names, role_codes,org_codes,offset,limit_value,skip_pagination)
        if result is not None:
            return result

    def onboard_users(self,users):
        result = userModel.onboard_users(users)
        if result is not None:
            return result

    def get_roles(self):
        result = userModel.get_roles_from_role_sheet()
        if result is not None:
            return result
