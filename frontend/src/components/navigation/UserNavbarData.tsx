import {HStack, Text} from "@chakra-ui/react";
import StringUtils from "../../utils/string-utils.ts";
import {User} from "../../entities/user/User.ts";

interface Props {
    user: User
}

const UserNavbarData = ({user}: Props) => {
    return (
        <HStack>
            <Text fontSize="sm">
                ({StringUtils.getTitleString(user.role)})
            </Text>
            <Text fontSize="sm" fontWeight="bold">
                {user.fullName}
            </Text>
        </HStack>
    )
}
export default UserNavbarData
