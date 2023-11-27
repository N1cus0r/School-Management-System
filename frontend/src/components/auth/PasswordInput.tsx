import React from "react";
import { Button, Input, InputGroup, InputRightElement } from "@chakra-ui/react";

interface Props {
  name: string;
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const PasswordInput = (props: Props) => {
  const [show, setShow] = React.useState(false);
  const handleClick = () => setShow(!show);

  return (
    <InputGroup size="md">
      <Input {...props} type={show ? "text" : "password"} />
      <InputRightElement width="4.5rem">
        <Button size="xs" onClick={handleClick}>
          {show ? "Hide" : "Show"}
        </Button>
      </InputRightElement>
    </InputGroup>
  );
};
export default PasswordInput;
