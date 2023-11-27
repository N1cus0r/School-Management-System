import {
  Button,
  FormControl,
  FormLabel,
  Input,
  Select,
  Stack,
  Text,
} from "@chakra-ui/react";
import PasswordInput from "../auth/PasswordInput.tsx";
import useCreateUserForm from "../../hooks/user/useCreateUserForm.ts";
import { Role } from "../../entities/user/Role.ts";

interface Props {
  userRole: Role;
  onSuccess: () => void;
}

const CreateUserForm = ({ userRole, onSuccess }: Props) => {
  const createUserForm = useCreateUserForm(userRole, onSuccess);

  return (
    <Stack spacing={4}>
      <FormControl id="gender">
        <FormLabel>Gender</FormLabel>
        <Select
          name="gender"
          value={createUserForm.values.gender}
          onChange={createUserForm.handleChange}
        >
          <option value="MALE">MALE</option>
          <option value="FEMALE">FEMALE</option>
        </Select>
        {createUserForm.touched.gender && createUserForm.errors.gender && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {createUserForm.errors.gender}
          </Text>
        )}
      </FormControl>
      <FormControl id="fullName">
        <FormLabel>Full Name</FormLabel>
        <Input
          type="text"
          name="fullName"
          placeholder="John Doe"
          value={createUserForm.values.fullName}
          onChange={createUserForm.handleChange}
        />
        {createUserForm.touched.fullName && createUserForm.errors.fullName && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {createUserForm.errors.fullName}
          </Text>
        )}
      </FormControl>
      <FormControl id="email">
        <FormLabel>Email Address</FormLabel>
        <Input
          type="email"
          name="email"
          placeholder="john.doe@example.com"
          value={createUserForm.values.email}
          onChange={createUserForm.handleChange}
        />
        {createUserForm.touched.email && createUserForm.errors.email && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {createUserForm.errors.email}
          </Text>
        )}
      </FormControl>
      <FormControl id="password">
        <FormLabel>Password</FormLabel>
        <PasswordInput
          name="password"
          value={createUserForm.values.password}
          onChange={createUserForm.handleChange}
        />
        {createUserForm.touched.password && createUserForm.errors.password && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {createUserForm.errors.password}
          </Text>
        )}
      </FormControl>
      <FormControl id="dateOfBirth">
        <FormLabel>Date Of Birth*</FormLabel>
        <Input
          type="date"
          name="dateOfBirth"
          value={createUserForm.values.dateOfBirth}
          onChange={createUserForm.handleChange}
        />
        {createUserForm.touched.dateOfBirth &&
          createUserForm.errors.dateOfBirth && (
            <Text color="red.500" fontSize="sm" mt={1}>
              {createUserForm.errors.dateOfBirth}
            </Text>
          )}
      </FormControl>
      <FormControl id="mobilePhone">
        <FormLabel>Mobile Phone*</FormLabel>
        <Input
          type="tel"
          name="mobilePhone"
          placeholder="+37379123456"
          value={createUserForm.values.mobilePhone}
          onChange={createUserForm.handleChange}
        />
        {createUserForm.touched.mobilePhone &&
          createUserForm.errors.mobilePhone && (
            <Text color="red.500" fontSize="sm" mt={1}>
              {createUserForm.errors.mobilePhone}
            </Text>
          )}
      </FormControl>
      <Button
        colorScheme={"blue"}
        variant={"solid"}
        isDisabled={!createUserForm.isValid}
        onClick={createUserForm.handleSubmit}
      >
        Create
      </Button>
    </Stack>
  );
};
export default CreateUserForm;
